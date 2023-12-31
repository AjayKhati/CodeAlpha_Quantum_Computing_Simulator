package com.ajkhati.quantumcomputingsimulator.tools;

import android.content.Context;

import com.ajkhati.quantumcomputingsimulator.math.VisualOperator;

import com.ajkhati.quantumcomputingsimulator.R;
import com.ajkhati.quantumcomputingsimulator.math.VisualOperator;

/**
 * An action that can either be done or undone
 */
public class Doable {

    VisualOperator visualOperator;
    public String name;
    DoableType type;
    public int index = -1;
    public int oldIndex = -1;
    VisualOperator oldOp;

    private Doable() {
    }

    public Doable(VisualOperator visualOperator, DoableType type, Context context) {
        this.visualOperator = visualOperator.copy();
        this.type = type;
        switch (type) {
            case ADD:
                name = context.getString(R.string.doable_add) + " " + visualOperator.getName();
                break;
            default:
                throw new IllegalArgumentException("Only ADD is allowed here");
        }
    }

    public Doable(VisualOperator visualOperator, DoableType type, Context context, int oldIndex, int newIndex) {
        this.visualOperator = visualOperator.copy();
        this.type = type;
        this.index = newIndex;
        this.oldIndex = oldIndex;
        switch (type) {
            case MOVE:
                name = context.getString(R.string.doable_move) + " " + visualOperator.getName();
                break;
            default:
                throw new IllegalArgumentException("Only MOVE is allowed here");
        }
    }

    public Doable(VisualOperator visualOperator, DoableType type, Context context, int index, VisualOperator oldOp) {
        this.visualOperator = visualOperator.copy();
        this.type = type;
        this.index = index;
        try {
            this.oldOp = oldOp.copy();
        } catch (Exception e) {
            this.oldOp = null;
        }
        switch (type) {
            case EDIT:
                name = context.getString(R.string.doable_edit) + " " + visualOperator.getName();
                break;
            case DELETE:
                name = context.getString(R.string.doable_delete) + " " + visualOperator.getName();
                break;
            case ADD:
                name = context.getString(R.string.doable_add) + " " + visualOperator.getName();
                break;
        }
    }

    public DoableType getType() {
        return type;
    }

    public VisualOperator getVisualOperator() {
        return visualOperator;
    }

    public VisualOperator oldOperator() {
        return oldOp;
    }

    public Doable copy() {
        Doable d = new Doable();
        try {
            d.oldOp = oldOp.copy();
        } catch (Exception e) {
        }
        d.index = index;
        d.oldIndex = oldIndex;
        d.type = type;
        try {
            d.visualOperator = visualOperator.copy();
        } catch (Exception e) {
        }
        d.name = name;
        return d;
    }

}
