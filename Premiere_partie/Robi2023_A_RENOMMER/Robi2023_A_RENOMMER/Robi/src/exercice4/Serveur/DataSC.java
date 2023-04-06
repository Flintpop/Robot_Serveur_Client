package exercice4.Serveur;

public class DataSC {
    String errMsg;
    int nLoops = 0;
    String cmd = null;
    String txt = null;

    public int getnLoops() {
        return nLoops;
    }

    public void setnLoops(int nLoops) {
        this.nLoops = nLoops;
    }

    String im = null;
    String env = null;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getSNode() {
        return SNode;
    }

    public void setSNode(String SNode) {
        this.SNode = SNode;
    }

    String SNode = null;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }
}
