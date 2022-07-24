/*
 * @(#)LayoutAdvice.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */
package com.dasd412.remake.api.controller;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.Writer;
import java.util.Map;

/**
 * mustache 사용 시 Layout 적용을 위한 ControllerAdvice
 */
@ControllerAdvice
public class LayoutAdvice {

    private final Mustache.Compiler compiler;

    @Autowired
    public LayoutAdvice(Mustache.Compiler compiler) {
        this.compiler = compiler;
    }

    /**
     * 만약 모델에서 "layout" 이라는 문자열이 있으면,
     *
     * @return layout.mustache 에 기재된 내용을 넣어준다. (화면 레이아웃)
     */
    @ModelAttribute("layout")
    public Mustache.Lambda layout(Map<String, Object> model) {
        return new Layout(compiler);
    }

    static class Layout implements Mustache.Lambda {

        String content;

        private final Mustache.Compiler compiler;

        public Layout(Mustache.Compiler compiler) {
            this.compiler = compiler;
        }

        /**
         * {{>layout/layout}} 이라는 코드가 mustache 파일에 있다면,
         * layout.mustache 내용을 넣어준다.
         */
        @Override
        public void execute(Template.Fragment fragment, Writer writer) {
            content = fragment.execute();
            compiler.compile("{{>layout/layout}}")
                    .execute(fragment.context(), writer);
        }
    }
}

