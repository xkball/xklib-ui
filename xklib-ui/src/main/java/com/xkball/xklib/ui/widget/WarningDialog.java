package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.container.BlockingContainerWidget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarningDialog extends BlockingContainerWidget {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarningDialog.class);
    
    private final Runnable onConfirm;
    private final Runnable onCancel;

    public WarningDialog(IComponent message, Runnable onConfirm, Runnable onCancel) {
        this.onConfirm = onConfirm == null ? () -> {} : onConfirm;
        this.onCancel = onCancel == null ? () -> {} : onCancel;
        
        Label messageLabel = new Label(message, 0xFFE2E8F0);
        messageLabel.setCSSClassName("warning_message");
        
        Button confirmButton = new Button("Confirm", this::confirm);
        confirmButton.setCSSClassName("warning_confirm");
        confirmButton.setColor(0xFFFFFFFF);
        
        Button cancelButton = new Button("Cancel", this::cancel);
        cancelButton.setCSSClassName("warning_cancel");
        cancelButton.setColor(0xFFE2E8F0);
        
        ContainerWidget panel = new ContainerWidget();
        panel.setCSSClassName("warning_panel");
        var actions = new ContainerWidget();
        actions.setCSSClassName("warning_actions");
        actions.addChild(cancelButton);
        actions.addChild(confirmButton);

        panel.addChild(messageLabel);
        panel.addChild(actions);
        panel.setOverflow(false);
        this.addChild(panel);

        this.asRootStyle("""
                WarningDialog {
                    size: 100% 100%;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                    background-color: 0x99000000;
                }
                .warning_panel {
                    size: content auto;
                    flex-direction: column;
                    align-items: stretch;
                    background-color: 0xFF0F172A;
                    border-style: solid;
                    border-width: 2px;
                    border-color: 0xFF334155;
                }
                .warning_message {
                    size: content 40rpx;
                    text-scale: expand-width;
                    text-align: center;
                    text-extra-width: 8rpx;
                    line-height: 18px;
                    margin-bottom: 10px;
                }
                .warning_actions {
                    size: 100% 16rpx;
                    flex-direction: row;
                    align-items: stretch;
                }
                .warning_cancel {
                    button-shape: rect;
                    button-bg-color: 0xFF334155;
                    button-hover-color: 0xFF475569;
                    size: 50% 100%;
                }
                .warning_confirm {
                    button-shape: rect;
                    button-bg-color: 0xFFDC2626;
                    button-hover-color: 0xFFB91C1C;
                    size: 50% 100%;
                }
                """);
    }
    
    public void display(){
        GuiSystem.INSTANCE.get().addScreenLayer(this);
    }

    private void confirm() {
        try {
            this.onConfirm.run();
        } catch (Exception e) {
            LOGGER.error("WarningDialog confirm callback failed", e);
        }
        closeSelf();
    }

    private void cancel() {
        try {
            this.onCancel.run();
        } catch (Exception e) {
            LOGGER.error("WarningDialog cancel callback failed", e);
        }
        closeSelf();
    }

    private void closeSelf() {
        var guiSystem = GuiSystem.INSTANCE.get();
        if (guiSystem != null) {
            guiSystem.removeScreenLayer(this);
        }
    }

}
