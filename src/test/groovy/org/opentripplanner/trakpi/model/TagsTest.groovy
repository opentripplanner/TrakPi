package org.opentripplanner.trakpi.model

import spock.lang.Specification
import spock.lang.Unroll

class TagsTest extends Specification {


    @Unroll
    def "parse '#text', expect '#expected'"() {

        when:
        def expr = new TagExpression(text)


        then:
        expr.toString() == expected

        where:
        text                          || expected
        "@ALL"                        || "@ALL"
        "@NONE"                       || "@NONE"
        "A"                           || "A"
        "!A"                          || "!A"
        "A&B"                         || "A & B"
        "A,B"                         || "A, B"
        "! A"                         || "!A"
        "A & B"                       || "A & B"
        "A , B"                       || "A, B"
        "A, B & !C"                   || "A, B & !C"
        "A, B & !C"                   || "A, B & !C"
        "A, B & !C"                   || "A, B & !C"
        "! A & B, B & C,!D,! B & ! A" || "!A & B, B & C, !D, !B & !A"
        " @ALL"                       || "@ALL"
        "@ALL "                       || "@ALL"
        "  @ALL  "                    || "@ALL"
        " @NONE"                      || "@NONE"
        "@NONE "                      || "@NONE"
        "  @NONE  "                   || "@NONE"
    }


    @Unroll
    def "evaluate expression '#exprStr', expect '#result'"() {
        given:
        List<TagSet> tags = [null, "A", "B", "C", "A B", "A C", "B C", "A B C"].collect { new TagSet(it) }

        when:
        def expr = new TagExpression(exprStr)

        then:
        filterTags(expr, tags) == result

        where:
        exprStr                | result
        "@NONE, A&B"           | "Ø AB ABC"
        "A"                    | "A AB AC ABC"
        "!A"                   | "Ø B C BC"
        "A&B"                  | "AB ABC"
        "A, B"                 | "A B AB AC BC ABC"
        "B&!A"                 | "B BC"
        "!A&!B&!C, B&!C, C&!A" | "Ø B C AB BC"
        "@ALL"                 | "Ø A B C AB AC BC ABC"
        "@NONE"                | "Ø"
        "@NONE"                | "Ø"
    }

    String filterTags(TagExpression expr, List<TagSet> tags) {
        tags.findAll {
            expr.eval(it.tags)
        }.join(" ")
    }

    static class TagSet {
        Set<Tag> tags = new HashSet<>()

        TagSet(String tagsStr) {
            if (tagsStr != null) {
                this.tags.addAll(tagsStr.split(/ /).collect { new Tag(it) })
            }
        }

        @Override
        String toString() {
            tags.isEmpty() ? "Ø" : tags.join("")
        }
    }
}