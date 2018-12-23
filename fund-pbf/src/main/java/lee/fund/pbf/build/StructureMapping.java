package lee.fund.pbf.build;

import lee.fund.pbf.a3.FieldType;
import lee.fund.pbf.structure.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/23 11:23
 * Desc:
 */
public class StructureMapping {
    private static final Map<FieldType, CodeStructure> FACTORY_MAP = new HashMap<>();

    static {
        FACTORY_MAP.put(FieldType.DOUBLE, new DoubleCodeStructure());
        FACTORY_MAP.put(FieldType.FLOAT, new FloatCodeStructure());
        FACTORY_MAP.put(FieldType.INT32, new Int32CodeStructure());
        FACTORY_MAP.put(FieldType.INT64, new Int64CodeStructure());
        FACTORY_MAP.put(FieldType.FIXED32, new Fixed32CodeStructure());
        FACTORY_MAP.put(FieldType.FIXED64, new Fixed64CodeStructure());
        FACTORY_MAP.put(FieldType.BOOL, new BoolCodeStructure());
        FACTORY_MAP.put(FieldType.STRING, new StringCodeStructure());
        FACTORY_MAP.put(FieldType.BYTES, new BytesCodeStructure());
        FACTORY_MAP.put(FieldType.UINT32, new Uint32CodeStructure());
        FACTORY_MAP.put(FieldType.UINT64, new Uint64CodeStructure());
        FACTORY_MAP.put(FieldType.SFIXED32, new Sfixed32CodeStructure());
        FACTORY_MAP.put(FieldType.SFIXED64, new Sfixed64CodeStructure());
        FACTORY_MAP.put(FieldType.SINT32, new Sint32CodeStructure());
        FACTORY_MAP.put(FieldType.SINT64, new Sint64CodeStructure());
        FACTORY_MAP.put(FieldType.OBJECT, new ObjectCodeStructure());
        FACTORY_MAP.put(FieldType.ENUM, new EnumCodeStructure());
    }
}
