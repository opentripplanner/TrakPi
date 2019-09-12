package org.opentripplanner.trakpi.domain.model

import java.lang.IllegalStateException
import java.util.regex.Pattern


private val TAG_LEGAL_REGEXP = Regex("[^@;,\\s\\p{Cntrl}]+")

private val ALL_TAGS_REGEXP = Regex("\\s*@ALL\\s*")
private val NONE_TAGS_REGEXP = Regex("\\s*@NONE\\s*")
private val OR_OPERATOR_PATTERN = Pattern.compile(",")
private val AND_OPERATOR_PATTERN = Pattern.compile("&")
private val TAGS_DELIMITER_PATTERN = Pattern.compile("\\s+")

/** Tags are used to group data to compare set of tags. */
class Tag(name: String) : NamedClass(name) {
    init {
        assert(name.matches(TAG_LEGAL_REGEXP)) {
            "Error! Tag contains illegal character. Tag: '$name', legal chars: ${TAG_LEGAL_REGEXP.pattern}"
        }
    }
    override fun toString() = name

    companion object {
        fun mapTagsToString(tags : Iterable<Tag>) = tags.map { it.name }.toSet()
        fun mapStringsToTags(tagStrings : Iterable<String>) = tagStrings.map { Tag(it) }.toSet()
    }
}

class TagExpression(exprText : String) {
    @Transient
    private val expr : TagExpr = ParseExpressionOp(exprText).parse()

    fun eval(tags : Set<Tag>) : Boolean {
        return expr.eval(tags)
    }

    override fun toString() = expr.toString()
}


/**
 * Parse a simple tag expression. Any number of whitespace characters can be used between each
 * token in the expression. The expresion grammar(G) is:
 * ```
 * G            ->  OrExpr | AndExpr | TagSet | ALL_TAGS
 * OrExpr       ->  OrExprBody ',' OrExprBody
 * OrExprBody   ->  AndExpr | TagSet | NO_TAGS
 * AndExpr      ->  TagSet '&' TagSet
 * TagSet       ->  TagSet TAG | TAG
 * TagExpr      ->  NotExpr | TAG
 * NotExpr      ->  '!' TAG
 *
 * TAG       ->  <A valid tag name>
 * ALL_TAGS  ->  '@ALL'
 * NO_TAGS   ->  '@NONE'
 * ```
 *
 * Operator precedence:
 * 1. OR ','
 * 2. AND '&'
 * 3. NOT '!'
 */
private class ParseExpressionOp(val text : String) {

    fun parse() : TagExpr {
        if(text.matches(ALL_TAGS_REGEXP)) return AllTagsExpr

        return parseOrExpr(text.split(OR_OPERATOR_PATTERN), 0)
    }

    fun parseOrExpr(tokens : List<String>, i : Int) : TagExpr {
        val lhs = parseOrBody(tokens[i])
        if(i + 1 == tokens.size) return lhs
        return  OrExpr(lhs, parseOrExpr(tokens, i + 1))
    }

    fun parseOrBody(token : String) : TagExpr {
        if(token.matches(NONE_TAGS_REGEXP)) return NoneTagsExpr
        return parseAndExpr(token.split(AND_OPERATOR_PATTERN), 0)
    }

    fun parseAndExpr(tokens : List<String>, i : Int) : TagExpr {
        val lhs = parseTagExpr(tokens[i])
        val next = i + 1
        return if(next == tokens.size) lhs else AndExpr(lhs, parseAndExpr(tokens, next))
    }

    fun parseTagExpr(token : String) : TagExpr {
        val tokens = token.split(TAGS_DELIMITER_PATTERN).map { it.trim() }.filter { it.isNotBlank() }

        if(tokens.size == 2 && tokens[0] == "!") {
            return NotTagExpr(Tag(tokens[1]))
        }
        if(tokens.size == 1) {
            if(tokens[0].startsWith("!")) return NotTagExpr(Tag(tokens[0].substring(1)))

            return MatchTagExpr(Tag(tokens[0]))
        }
        throw parseError("The '$token' is not a valid expression, expected '!' or a <tag>")
    }

    private fun parseError(msg: String) = IllegalStateException("Parse error! $msg. Text: '$text'")
}


private interface TagExpr {
    fun eval(tags : Set<Tag>) : Boolean
}

private class MatchTagExpr(val tag : Tag) : TagExpr {
    override fun eval(tags : Set<Tag>) : Boolean {
        return tags.contains(tag)
    }
    override fun toString() = "$tag"
}

private class NotTagExpr(val tag : Tag) : TagExpr {
    override fun eval(tags : Set<Tag>) : Boolean {
        return !tags.contains(tag)
    }
    override fun toString() = "!$tag"
}

private class AndExpr(val lhs : TagExpr, val rhs : TagExpr) : TagExpr {
    override fun eval(tags : Set<Tag>) : Boolean {
        return lhs.eval(tags) && rhs.eval(tags)
    }
    override fun toString() = "${lhs}&${rhs}"
}

private class OrExpr(val lhs : TagExpr, val rhs : TagExpr) : TagExpr {
    override fun eval(tags : Set<Tag>) : Boolean {
        return lhs.eval(tags) || rhs.eval(tags)
    }
    override fun toString() = "${lhs}, ${rhs}"
}

private object AllTagsExpr : TagExpr {
    override fun eval(tags : Set<Tag>)  = true
    override fun toString() = "@ALL"
}

private object NoneTagsExpr : TagExpr {
    override fun eval(tags : Set<Tag>)  = tags.isEmpty()
    override fun toString() = "@NONE"
}
