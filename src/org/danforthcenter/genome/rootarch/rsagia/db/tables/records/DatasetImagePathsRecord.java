/*
 * This file is generated by jOOQ.
*/
package org.danforthcenter.genome.rootarch.rsagia.db.tables.records;


import javax.annotation.Generated;

import org.danforthcenter.genome.rootarch.rsagia.db.tables.DatasetImagePaths;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DatasetImagePathsRecord extends UpdatableRecordImpl<DatasetImagePathsRecord> implements Record3<Integer, String, String> {

    private static final long serialVersionUID = 263580725;

    /**
     * Setter for <code>rsa_gia.dataset_image_paths.dataset_id</code>.
     */
    public void setDatasetId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>rsa_gia.dataset_image_paths.dataset_id</code>.
     */
    public Integer getDatasetId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>rsa_gia.dataset_image_paths.image_type</code>.
     */
    public void setImageType(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>rsa_gia.dataset_image_paths.image_type</code>.
     */
    public String getImageType() {
        return (String) get(1);
    }

    /**
     * Setter for <code>rsa_gia.dataset_image_paths.image_path</code>.
     */
    public void setImagePath(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>rsa_gia.dataset_image_paths.image_path</code>.
     */
    public String getImagePath() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record2<Integer, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Integer, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Integer, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return DatasetImagePaths.DATASET_IMAGE_PATHS.DATASET_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return DatasetImagePaths.DATASET_IMAGE_PATHS.IMAGE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return DatasetImagePaths.DATASET_IMAGE_PATHS.IMAGE_PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getDatasetId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getImageType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getImagePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getDatasetId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getImageType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getImagePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetImagePathsRecord value1(Integer value) {
        setDatasetId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetImagePathsRecord value2(String value) {
        setImageType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetImagePathsRecord value3(String value) {
        setImagePath(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetImagePathsRecord values(Integer value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DatasetImagePathsRecord
     */
    public DatasetImagePathsRecord() {
        super(DatasetImagePaths.DATASET_IMAGE_PATHS);
    }

    /**
     * Create a detached, initialised DatasetImagePathsRecord
     */
    public DatasetImagePathsRecord(Integer datasetId, String imageType, String imagePath) {
        super(DatasetImagePaths.DATASET_IMAGE_PATHS);

        set(0, datasetId);
        set(1, imageType);
        set(2, imagePath);
    }
}