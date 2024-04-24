package com.owofurry.furry.img.elasticsearch;

import com.owofurry.furry.img.entity.Keyword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serial;
import java.io.Serializable;

@Document(indexName = "keyword")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class KeywordDoc implements Serializable {

    @Serial
    private static final long serialVersionUID = 8624017328181872411L;
    @Id
    @Field(type = FieldType.Long)
    Long id;

    @Field(type = FieldType.Text, name = "keyword",
            analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    String keyword;

    public KeywordDoc(Keyword keyword) {
        this(keyword.getId(), keyword.getKeyword());
    }

    public KeywordDoc(String s) {
        this(null, s);
    }
}
