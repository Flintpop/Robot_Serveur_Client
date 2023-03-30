package exercice4;

public class DataCS {
    String cmd = null;
    String txt = null;
    String all = null;

    public String getAll() {
        return cmd + " " + txt;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
}
