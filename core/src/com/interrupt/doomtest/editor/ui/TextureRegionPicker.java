package com.interrupt.doomtest.editor.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.interrupt.doomtest.gfx.Art;
import com.interrupt.doomtest.levels.Surface;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public abstract class TextureRegionPicker extends Window {
    float spriteSize = 60f;
    Actor previousKeyboardFocus, previousScrollFocus;
    FocusListener focusListener;

    ScrollPane pane;
    Table buttonLayout;
    Cell paneCell;

    final Array<Surface> regions;

    public TextureRegionPicker(String title, Skin skin, Array<Surface> regions) {
        super(title, skin);

        this.regions = regions;

        buttonLayout = new Table();
        pane = new ScrollPane(buttonLayout, skin);
        pane.setFillParent(false);
        pane.setFadeScrollBars(false);
        row();

        paneCell = add(pane);

        makeRegionButtons();

        focusListener = new FocusListener() {
            public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
                if (!focused) focusChanged(event);
            }

            public void scrollFocusChanged (FocusEvent event, Actor actor, boolean focused) {
                if (!focused) focusChanged(event);
            }

            private void focusChanged (FocusEvent event) {
                Stage stage = getStage();
                if (stage != null && stage.getRoot().getChildren().size > 0
                        && stage.getRoot().getChildren().peek() == TextureRegionPicker.this) { // Dialog is top most actor.
                    Actor newFocusedActor = event.getRelatedActor();
                    if (newFocusedActor != null && !newFocusedActor.isDescendantOf(TextureRegionPicker.this) &&
                            !(newFocusedActor.equals(previousKeyboardFocus) || newFocusedActor.equals(previousScrollFocus)) )
                        event.cancel();
                }
            }
        };

        paneCell.width(getWidth() + 30f);
        pack();

        setHeight(578f);

        if(buttonLayout.getHeight() < getHeight()) {
            setHeight(buttonLayout.getHeight() + 64f);
        }

        setWidth(getWidth() + 30f);
    }

    protected void makeRegionButtons() {
        buttonLayout.reset();

        int num = 0;
        for(Surface region : regions) {
            ImageButton button = new ImageButton(new TextureRegionDrawable(region.getTextureRegion()));
            button.getImage().setScaling(Scaling.fill);
            button.getImageCell().width(spriteSize).height(spriteSize);

            button(button, num++, region);

            if(num % 8 == 0) {
                buttonLayout.row();
            }
        }
        buttonLayout.pack();
        pack();
    }

    protected void setStage (Stage stage) {
        if (stage == null)
            addListener(focusListener);
        else
            removeListener(focusListener);
        super.setStage(stage);
    }

    protected InputListener ignoreTouchDown = new InputListener() {
        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            event.cancel();
            return false;
        }
    };

    public void button(Button button, final Integer value, final Surface region) {
        buttonLayout.add(button).pad(4f);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                result(value, region);
            }
        });
    }

    public void show(Stage stage) {
        setStage(stage);
        addAction(sequence(Actions.alpha(0), Actions.fadeIn(0.35f, Interpolation.fade)));

        clearActions();
        removeCaptureListener(ignoreTouchDown);

        previousKeyboardFocus = null;
        Actor actor = stage.getKeyboardFocus();
        if (actor != null && !actor.isDescendantOf(this)) previousKeyboardFocus = actor;

        previousScrollFocus = null;
        actor = stage.getScrollFocus();
        if (actor != null && !actor.isDescendantOf(this)) previousScrollFocus = actor;

        setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));

        stage.setScrollFocus(pane);
    }

    public void hide() {
        hide(sequence(Actions.alpha(1f), Actions.fadeOut(0f, Interpolation.fade)));
    }

    public void hide (Action action) {
        Stage stage = getStage();
        if (stage != null) {
            removeListener(focusListener);
            if (previousKeyboardFocus != null && previousKeyboardFocus.getStage() == null) previousKeyboardFocus = null;
            Actor actor = stage.getKeyboardFocus();
            if (actor == null || actor.isDescendantOf(this)) stage.setKeyboardFocus(previousKeyboardFocus);

            if (previousScrollFocus != null && previousScrollFocus.getStage() == null) previousScrollFocus = null;
            actor = stage.getScrollFocus();
            if (actor == null || actor.isDescendantOf(this)) stage.setScrollFocus(previousScrollFocus);
        }
        if (action != null) {
            addCaptureListener(ignoreTouchDown);
            addAction(sequence(action, Actions.removeListener(ignoreTouchDown, true), Actions.removeActor()));
        } else
            remove();
    }

    public abstract void result(Integer value, Surface region);
}
