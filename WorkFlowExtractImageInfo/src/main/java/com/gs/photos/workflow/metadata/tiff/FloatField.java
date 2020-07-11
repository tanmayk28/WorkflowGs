package com.gs.photos.workflow.metadata.tiff;

import java.util.Arrays;

import com.gs.photo.workflow.exif.Tag;
import com.gs.photos.workflow.metadata.fields.SimpleAbstractField;

public class FloatField extends TiffField<float[]> {
    private static final long serialVersionUID = 1L;

    public FloatField(
        Tag ifdTagParent,

        Tag tag,
        SimpleAbstractField<float[]> underLayingField,
        short tagValue
    ) {
        super(ifdTagParent,
            tag,
            underLayingField,
            tagValue,
            underLayingField.getOffset());
    }

    @Override
    public String getDataAsString() { return Arrays.toString(this.underLayingField.getData()); }
}