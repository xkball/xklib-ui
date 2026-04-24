package com.xkball.xklib.ap;

import com.google.auto.service.AutoService;
import com.xkball.xklib.ap.annotation.GuiWidgetClass;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_25)
@SupportedAnnotationTypes({GuiWidgetClassProcessor.ANNOTATION_PROCESSOR})
public class GuiWidgetClassProcessor extends ClassPathOutputProcessor {
    
    public static final String ANNOTATION_PROCESSOR = "com.xkball.xklib.ap.annotation.GuiWidgetClass";
    
    public GuiWidgetClassProcessor() {
        super(ANNOTATION_PROCESSOR, GuiWidgetClass.class);
    }
}