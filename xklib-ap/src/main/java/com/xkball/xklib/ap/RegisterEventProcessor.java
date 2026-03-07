package com.xkball.xklib.ap;

import com.xkball.xklib.ap.annotation.RegisterEvent;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_25)
@SupportedAnnotationTypes({RegisterEventProcessor.ANNOTATION_PROCESSOR})
public class RegisterEventProcessor extends AbstractProcessor {
    
    public static final String ANNOTATION_PROCESSOR = "com.xkball.xklib.ap.annotation.RegisterEvent";
    private static final String PATH = "META-INF/services/" + ANNOTATION_PROCESSOR;
    private Filer filer;
    
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
    }
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) return false;
        var classes = roundEnv.getElementsAnnotatedWith(RegisterEvent.class);
        try {
            var file = filer.getResource(StandardLocation.CLASS_OUTPUT,"",PATH);
            file.delete();
            file = filer.createResource(StandardLocation.CLASS_OUTPUT,"",PATH);
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