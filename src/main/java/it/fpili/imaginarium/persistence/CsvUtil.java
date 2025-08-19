package it.fpili.imaginarium.persistence;

/**
 * Small CSV escaping utilities (RFC4180-ish).
 */
final class CsvUtil {
    private CsvUtil(){}
    static String esc(String s){
        if (s == null) return "";
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String v = s.replace("\"","\"\"");
        return needQuotes ? "\"" + v + "\"" : v;
    }
    static String[] parseLine(String line){
        // Simple parser for 4 columns; adequate for student-level exercise.
        StringBuilder cur = new StringBuilder();
        boolean inQ=false; java.util.List<String> out=new java.util.ArrayList<>(4);
        for(int i=0;i<line.length();i++){
            char c=line.charAt(i);
            if(inQ){
                if(c=='"'){
                    if(i+1<line.length() && line.charAt(i+1)=='"'){ cur.append('"'); i++; }
                    else inQ=false;
                } else cur.append(c);
            } else {
                if(c=='"') inQ=true;
                else if(c==','){ out.add(cur.toString()); cur.setLength(0); }
                else cur.append(c);
            }
        }
        out.add(cur.toString());
        return out.toArray(String[]::new);
    }
}
