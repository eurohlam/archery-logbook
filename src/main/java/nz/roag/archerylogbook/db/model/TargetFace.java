package nz.roag.archerylogbook.db.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TargetFace {

    TF_122cm("122cm"),
    TF_80cm("80cm"),
    TF_60cm("60cm"),
    TF_40cm("40cm"),
    TF_MULTI_SPOT("Multi-spot");


    private String targetFace;

    TargetFace(String targetFace) {
        this.targetFace = targetFace;
    }

    @Override
    @JsonValue
    public String toString() {
        return this.targetFace;
    }

    public static TargetFace fromValue(String value){
        for (TargetFace tf:TargetFace.values()) {
            if (tf.toString().equals(value)) {
                return tf;
            }
        }
        throw new IllegalArgumentException("Illegal value of argument: " + value);
    }
}
