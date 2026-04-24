package com.xkball.xklib.ap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

public class ClassPathOutputProcessor extends AbstractProcessor {
    
    private final String name;
    private final Class<? extends Annotation> annoClass;
    private static final String PATH_BASE = "META-INF/services/";
    private Filer filer;
    
    public ClassPathOutputProcessor(String name, Class<? extends Annotation> annoClass) {
        this.name = name;
        this.annoClass = annoClass;
    }
    
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
    }
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) return false;
        var classes = roundEnv.getElementsAnnotatedWith(annoClass);
        try {
            var file = filer.getResource(StandardLocation.CLASS_OUTPUT,"",PATH_BASE + name);
            file.delete();
            file = filer.createResource(StandardLocation.CLASS_OUTPUT,"",PATH_BASE + name);
            try (var writer = file.openWriter()) {
                for (var clazz : classes) {
                    writer.write(clazz.toString());
                    writer.write("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return true;
    }
}
