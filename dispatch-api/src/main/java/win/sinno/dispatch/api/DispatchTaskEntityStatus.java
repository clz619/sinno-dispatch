package win.sinno.dispatch.api;

/**
 * 任务实体状态
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 16:10.
 */
public enum DispatchTaskEntityStatus {

    /**
     * 0,new,新建
     */
    NEW(0, "new", "新建"),

    /**
     * 1,success,成功
     */
    SUCCESS(1, "success", "成功"),

    /**
     * 2,fail,失败
     */
    FAIL(2, "fail", "失败");

    DispatchTaskEntityStatus(int code, String valueEn, String valueCn) {
        this.code = code;
        this.valueEn = valueEn;
        this.valueCn = valueCn;
    }

    private int code;

    private String valueEn;

    private String valueCn;

    public int getCode() {
        return code;
    }

    public String getValueEn() {
        return valueEn;
    }

    public String getValueCn() {
        return valueCn;
    }
}
