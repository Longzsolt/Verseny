package feladat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Feladat {
    
    private static String feldolgozCimsor(String str) {
        int hcount=0;
        
        for(int i=0;i<str.length();i++){
            if(str.charAt(i) == '#')hcount++;
            else if(str.charAt(i)== ' ')break;
            else return feldolgoz(str);
        }
        
        String html = "<h"+hcount+">"+feldolgoz(str.substring(hcount+1))+"</h"+hcount+">";
        return html;
    }
    
    public static String feldolgoz(String str){
        StringBuilder html = new StringBuilder();
        String specialChrs = "*()[]\\";
        
        Stack<String> tags = new Stack();
        
        char[] chrs = str.toCharArray();
        
        for(int i=0;i<str.length();i++){
            switch(chrs[i]){
            
                case '\\':
                    if(i<str.length()-1 && (specialChrs.contains(chrs[i+1]+""))){
                        html.append(chrs[i+1]);
                        i++;
                    }
                    break;
                
                case '<':
                    html.append("&lt;");
                    break;
                    
                case '>':
                    html.append("&gt;");
                    break;
                    
                case '*':
                    boolean single = (i<str.length()-1 && chrs[i+1]!='*');
                        Stack<String> temp = new Stack();
                        boolean is_end = (tags.search(single?"*":"**") != -1);
                        
                        if(is_end){
                            while(!tags.empty()){
                                if(tags.peek() == (single?"*":"**")){
                                    tags.pop();
                                    html.append(single?"</em>":"</strong>");
                                    break;
                                }
                                String crtTag = tags.pop();
                                html.append(getETag(crtTag));
                                temp.push(crtTag);
                            }

                            while(!temp.empty()){
                                tags.push(temp.pop());
                                html.append(getSTag(tags.peek()));
                            }
                        }
                        else {
                            tags.push(single?"*":"**");
                            html.append(single?"<em>":"<strong>");
                        }
                        if(!single)i++;
                    break;
                case '[':
                    StringBuilder lszoveg = new StringBuilder();
                    while(chrs[i+1]!=']'){
                        i++;
                        lszoveg.append(chrs[i]);
                    }
                    i+=3;
                    
                    html.append("<a href=\"");
                    while(chrs[i]!=')'){
                        if(chrs[i]!='"')
                            html.append(chrs[i]);
                        else html.append("%22");
                        i++;
                    }
                    html.append("\">");
                    String feldSzov = feldolgoz(lszoveg.toString());
                    html.append(feldSzov);
                    html.append("</a>");
                    
                    break;
                    
                default: html.append(chrs[i]);
            }
        }
        
        while(!tags.empty()){
            html.append(getETag(tags.pop()));
        }
        
        return html.toString();
    }
    
    public static String atalakit(String fajlnev) throws Exception{
        BufferedReader br;
        br = new BufferedReader(new FileReader(new File(fajlnev)));
        
        StringBuilder sb=new StringBuilder();
        StringBuilder html = new StringBuilder();
        String crtSor;
        
        while((crtSor = br.readLine()) != null){
            if(crtSor.matches("#(.*)")){
                html.append(feldolgozCimsor(crtSor));
            }
            
            else if(crtSor.trim().matches("")){
                if(!sb.toString().matches("")){
                    html.append("<p>"+feldolgoz(sb.toString())+"</p>");
                }
                sb.delete(0, sb.length());
            }
            else sb.append(crtSor);
        }
        
        if(!sb.toString().matches(""))
            html.append("<p>"+feldolgoz(sb.toString())+"</p>");
        sb.delete(0, sb.length());
        
        return html.toString();
    }
    
    public static void main(String[] args) {
        try {
            //System.out.print(atalakit("input.md"));
            BufferedWriter bw;
            bw = new BufferedWriter(new FileWriter(new File("output.html")));
            bw.write("<html>"+"<body>");
            bw.write(atalakit("input.md"));
            bw.write("</body>"+"</html>");
            bw.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    
    public static String getSTag(String str)
    {
        if(str.length() == 1 && str.charAt(0) == '*') return "<em>";
        else if(str.length() == 1) return "<a>";
        if(str.length() == 2) return "<strong>";
        return "";
    }
    
    public static String getETag(String str)
    {
        if(str.length() == 1 && str.charAt(0) == '*') return "</em>";
        else if(str.length() == 1) return "</a>";
        if(str.length() == 2) return "</strong>";
        return "";
    }
}