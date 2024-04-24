package com.owofurry.furry.img.elasticsearch;

import com.owofurry.furry.img.entity.FileRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Document(indexName = "keyword_image")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class KeywordImageDoc implements Serializable {

    @Id
    @Field(type = FieldType.Long)
    Long fileId;
    @Field(type = FieldType.Text, name = "keyword",
            analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    String keywords;
    @Field(index = false)
    private String title;
    @Field(index = false)
    private String introduce;
    @Field(index = false)
    private Integer uploadUser;
    @Field(index = false)
    private Long loverNum;
    @Field(index = false)
    private Date uploadDate;
    @Field(index = false)
    private String filePath;

    public KeywordImageDoc(FileRecord fileRecord) {
        this(fileRecord.getFileId(),
                fileRecord.getKeywords(),
                fileRecord.getTitle(),
                fileRecord.getIntroduce(),
                fileRecord.getUploadUser(),
                fileRecord.getLoverNum(),
                fileRecord.getUploadDate(),
                fileRecord.getFilePath());
    }
}
