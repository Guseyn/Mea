import org.apache.commons.lang3.StringUtils;

public class Main {
    public static void main(String[] args) {
        StringUtils.difference("return new EqualsBuilder().append(this.numero, other.numero).append(this.dv, other.dv).isEquals()", "Objects.equal(this.numero, other.numero) && Objects.equal(this.dv, other.dv)");
    }
}
