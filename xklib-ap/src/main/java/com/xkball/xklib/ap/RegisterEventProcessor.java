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
import java.lang.annotation.Annotation;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_25)
@SupportedAnnotationTypes({RegisterEventProcessor.ANNOTATION_PROCESSOR})
public class RegisterEventProcessor extends ClassPathOutputProcessor {
    
    public static final String ANNOTATION_PROCESSOR = "com.xkball.xklib.ap.annotation.RegisterEvent";
    
    public RegisterEventProcessor() {
        super(ANNOTATION_PROCESSOR, RegisterEvent.class);
    }
}