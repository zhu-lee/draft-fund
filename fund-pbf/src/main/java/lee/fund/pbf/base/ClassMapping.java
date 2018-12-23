package lee.fund.pbf.base;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/23 12:41
 * Desc:
 */
public enum ClassMapping {
    LIST("java/util/List"),
    ARRAYLIST_FULL_NAME("java/util/ArrayList"),
    INTEGER("java/lang/Integer"),
    LONG("java/lang/Long"),
    FLOAT("java/lang/Float"),
    DOUBLE("java/lang/Double"),
    BOOLEANE("java/lang/Boolean"),
    DATE("java/util/Date"),
    LOCALDATE("java/time/LocalDate"),
    LOCALDATETIME("java/time/LocalDateTime"),
    DAYOFWEEK("java/time/DayOfWeek"),
    CODEC("mtime/lark/pb/Codec"),
    IOEXCEPTION("java/io/IOException"),
    GENCODEC("lee/fund/pbf/base/GenCodec"),
    CODEDOUTPUTSTREAM("com/google/protobuf/CodedOutputStream"),
    CODEDINPUTSTREAM("com/google/protobuf/CodedInputStream");

    private final String fullName;

    public String fullName() {
        return fullName;
    }

    ClassMapping(String fullName) {
        this.fullName = fullName;
    }
}
