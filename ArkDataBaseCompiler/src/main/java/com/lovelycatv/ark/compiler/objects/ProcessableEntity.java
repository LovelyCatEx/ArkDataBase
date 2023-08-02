package com.lovelycatv.ark.compiler.objects;

import com.lovelycatv.ark.runtime.annotations.Column;
import lombok.Data;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ProcessableEntity {
    private DeclaredType declaredEntityType;
    private String tableName;
    private List<EntityColumn> entityColumnList = new ArrayList<>();



    @Data
    public static class EntityColumn {
        private Element element;
        private Column column;
    }

    public static class Controller {
        private final List<ProcessableEntity> entityList = new ArrayList<>();

        public List<ProcessableEntity> getEntityList() {
            return entityList;
        }
    }
}
