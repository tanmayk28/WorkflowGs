package com.gs.workflow.coprocessor;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

public class KeyWordMetadataStringCoprocessor extends AbstractMetadataStringCoprocessor {

    private static final byte[] SOURCE_FAMILY             = "keywords".getBytes(Charset.forName("UTF-8"));
    public static final int     FIXED_WIDTH_KEYWORD       = 64;
    public static final int     FIXED_WIDTH_IMAGE_ID      = 64;
    public static final int     FIXED_WIDTH_CREATION_DATE = 8;

    @Override
    protected String getTablePageForMetadata() { return "page_images_keyword"; }

    @Override
    protected String getTableSource() { return "image_thumbnail"; }

    @Override
    protected String getTableImagesOfMetaData() { return "images_keyword"; }

    @Override
    protected String getTableMetaData() { return "keywords"; }

    @Override
    protected byte[] getTableSourceFamily() { return KeyWordMetadataStringCoprocessor.SOURCE_FAMILY; }

    @Override
    protected byte[] toTablePageRowKey(String t, long pageNumber) {
        byte[] retValue = Arrays.copyOf(
            t.getBytes(Charset.forName("UTF-8")),
            KeyWordMetadataStringCoprocessor.FIXED_WIDTH_KEYWORD
                + KeyWordMetadataStringCoprocessor.FIXED_WIDTH_CREATION_DATE);
        Bytes.putLong(retValue, KeyWordMetadataStringCoprocessor.FIXED_WIDTH_KEYWORD, pageNumber);
        return retValue;
    }

    @Override
    protected long extractPageNumber(byte[] rowKey) {
        return Bytes.toLong(rowKey, KeyWordMetadataStringCoprocessor.FIXED_WIDTH_KEYWORD);
    }

    @Override
    protected byte[] getRowKeyForMetaDataTable(String metadata) { // TODO Auto-generated method stub
        return Arrays
            .copyOf(metadata.getBytes(Charset.forName("UTF-8")), AlbumMetadataStringCoprocessor.FIXED_WIDTH_KEYWORD);
    }

    @Override
    protected long extractDateOfRowKeyOfTabkeSourceToIndex(byte[] rowKey) {
        return Bytes.toLong(rowKey, AbstractPageProcessor.FIXED_WIDTH_REGION_SALT);
    }

    @Override
    protected String extractMetadataValue(byte[] rowKey) {
        return new String(rowKey, 0, KeyWordMetadataStringCoprocessor.FIXED_WIDTH_KEYWORD);
    }

    @Override
    protected long extractPageNumber(byte[] rowKey, int pos, int length) {
        return Bytes.toLong(rowKey, KeyWordMetadataStringCoprocessor.FIXED_WIDTH_KEYWORD + pos);

    }

    @Override
    protected String extractMetadataValue(byte[] rowKey, int pos, int length) {
        return new String(rowKey, 0, KeyWordMetadataStringCoprocessor.FIXED_WIDTH_KEYWORD + pos);
    }

    @Override
    protected boolean isAllowedForTableSource(Put put) {
        List<Cell> albumCells = put.getFamilyCellMap()
            .get(KeyWordMetadataStringCoprocessor.SOURCE_FAMILY);
        if ((albumCells != null) && (albumCells.size() > 0)) {
            List<Cell> cells = put.getFamilyCellMap()
                .get(ImagesPageCoprocessor.TABLE_SOURCE_THUMBNAIL);

            if ((cells != null) && (cells.size() > 0)) {
                return cells.stream()
                    .peek(
                        (c) -> AlbumMetadataStringCoprocessor.LOGGER
                            .info("Exam {} ", new String(CellUtil.cloneQualifier(c))))
                    .filter((c) -> {
                        Integer key = Integer.parseInt(new String(CellUtil.cloneQualifier(c)));
                        return (key == 1);
                    })
                    .findFirst()
                    .isPresent();
            } else {
                ImagesPageCoprocessor.LOGGER
                    .warn("Unable to find some thumbnail for {} ", AbstractPageProcessor.toHexString(put.getRow()));
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean shouldFamilyInsourceBeRecordedInMetadata(byte[] familySource) {
        return (Objects.deepEquals(KeyWordMetadataStringCoprocessor.SOURCE_FAMILY, familySource));
    }

    @Override
    protected String getCoprocName() { return "COPROC-KEYWORD"; }

}
