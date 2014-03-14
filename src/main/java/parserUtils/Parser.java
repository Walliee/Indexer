package parserUtils;

import java.util.regex.Pattern;


/**
 * This is a Java port of the parser written in C by Xiaohui Long
 * Slightly modified to return offset data. URLs get offset of -1
 * <p>
 * Java port maintained by Alek Dembowski (adembo01 at utopia.poly.edu)
 */
public class Parser {
    private interface Tags{
        static final int PTAG_B = 1;
        static final int PTAG_I = 2;
        static final int PTAG_H = 3;
        static final int PTAG_TITLE = 4;
        static final int PTAG_SCRIPT = 5;
    }

    public static void main(String[] args) {
        System.out.println("Testing: http://fun.com/fun.html");
        StringBuilder builder = new StringBuilder();
        Pattern newLineSplitPattern = Pattern.compile("\n");
        parseUrl("http://fun.com/fun.html", builder);
        //parseUrl("across.co.nz/Canadian%27sView.htm", builder);
        //parsePage("", "This is <b>so</b>    <H6>very very <b><em>much</h6> fun</em></b>", builder);
        String[] lines = newLineSplitPattern.split(builder);
        System.out.println(lines.length);
        for(String temp:lines){
        	System.out.println(temp);
        }
        //System.out.println(builder);
    }

    /**
     * Parses url and doc, storing result in builder. <b> Not thread safe </b>
     * <p>
     * Parsed results are in the form of
     * <ul>
     *   <li> <code> &lt;word&gt; &lt;context&gt; (line break) </code>
     *   <li> <code> &lt;word&gt; &lt;context&gt; (line break) </code>
     * </ul>
     * <p>
     * For performance, this class uses StringBuilder instead of StringBuffer. If syncronization
     * is required, provide syncronization outside of calling this class.
     *
     * @param url The url of the page being parsed
     * @param doc The page (including headers) of the page being parsed
     * @param builder The place to store the results.
     *
     */
    public static int parseDoc(String url, String doc, StringBuilder builder) {
        doc = initDoc(doc);
        if (doc == null)
            return 0;

        parseUrl(url, builder);
        parsePage(url, doc, builder);

        return 0;
    }

    /**
     * Assumes tag start with a '<' character. Back tags would then be "</"
     */
    static boolean isBackTag(String tag) {
        return tag.charAt(1) == '/';
    }

    static boolean isIndexable(char c) {
        return ((c >= '0' && c <= '9') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= 'a' && c <= 'z'));
    }

    static boolean isEndTag(String tag, int i) {
        return ('>' == tag.charAt(i) || Character.isWhitespace(tag.charAt(i)));
    }

    /**
     * Assumes tag starts with a "<" and ends with either a ">" or a whitespace character
     */
    static int parseTag(String tag) {
        int i = 1;

        if (isBackTag(tag)) {
            i++;
        }

        switch (tag.charAt(i)) {
            case 'b':
            case 'B':
            case 'i':
            case 'I':
                if (!isEndTag(tag, i + 1))
                    return 0;
                if ((tag.charAt(i) == 'b') || (tag.charAt(i) == 'B'))
                    return Tags.PTAG_B;
                return Tags.PTAG_I;

            case 'e':
            case 'E':
                i++;
                if (((tag.charAt(i) == 'm') || (tag.charAt(i) == 'M')) && isEndTag(tag, i + 1))
                    return Tags.PTAG_I;
                return 0;

            case 'h':
            case 'H':
                i++;
                if (((tag.charAt(i) >= '1') && (tag.charAt(i) <= '6')) && (isEndTag(tag, i + 1)))
                    return Tags.PTAG_H;
                return 0;

            case 't':
            case 'T':
                i++;
                if ("itle".equalsIgnoreCase(safeSubstring(tag, i, i + 4)) && isEndTag(tag, i + 4)) {
                    return Tags.PTAG_TITLE;
                }
                return 0;

            case 's':
            case 'S':
                i++;
                if ("trong".equalsIgnoreCase(safeSubstring(tag, i, i + 5)) && isEndTag(tag, i + 5)) {
                    return Tags.PTAG_B;
                } else
                if ("cript".equalsIgnoreCase(safeSubstring(tag, i, i + 5)) && isEndTag(tag, i + 5)) {
                    return Tags.PTAG_SCRIPT;
                }
                return 0;

            default:
                break;
        }

        return 0;
    }

    private static String safeSubstring(String str, int start, int end){
        if(end > str.length()){
            return str;
        } else {
            return str.substring(start, end);
        }
    }

    static void parseUrl(String url, StringBuilder buf){
        int index = 0;
        char urlCharacter = url.charAt(index);
        while (index < url.length()) {
            if (!isIndexable(urlCharacter)) {
                index++;
                continue;
            }

            int wordEndIndex = index;
            while (wordEndIndex < url.length() && isIndexable(url.charAt(wordEndIndex))) {
                wordEndIndex++;
            }

            if(index != wordEndIndex){
                buf.append(url.substring(index, wordEndIndex).toLowerCase()).append(" U\n");
            }
            index = wordEndIndex + 1;
        }
    }

    /**
     * @param doc
     * @return null if failed, actual document after the header if successful
     */
    static String initDoc(String doc){
        if(!"HTTP/".equalsIgnoreCase(doc.substring(0, 5))){
            return null;
        }

        int index = doc.indexOf(' ');

        if(index == -1){
            return null;
        } else if(!"200".equals(doc.substring(index+1, index + 4))){
            return null;
        }

        index = doc.indexOf("\r\n\r\n");
        if(index == -1){
            return null;
        }

        return doc.substring(index + 4);
    }

    static void parsePage(String url, String doc, StringBuilder builder) {
        int docIndex = 0;
        int tagIndex = 0;
        int wordIndex;

        boolean backTag;
        boolean inTag = false;
        boolean inScript = false;
        boolean bold = false;
        boolean italic = false;
        boolean header = false;
        boolean title = false;

        int ret;
        int docLength = doc.length();
        while (docIndex < docLength)
        {
            char docChar = doc.charAt(docIndex);
            if (!isIndexable(docChar))
            {
                if (docChar != '>') {
                    if (docChar == '<') {
                        tagIndex = docIndex;
                        inTag = true;
                    }
                    docIndex++;
                    continue;
                }

                docIndex++;
                String tag = doc.substring(tagIndex, docIndex);
                backTag = isBackTag(tag);
                ret = parseTag(tag);
                switch (ret) {
                    case Tags.PTAG_B:
                        bold = !backTag;
                        break;

                    case Tags.PTAG_I:
                        italic = !backTag;
                        break;

                    case Tags.PTAG_H:
                        header = !backTag;
                        break;

                    case Tags.PTAG_TITLE:
                        title = !backTag;
                        break;

                    case Tags.PTAG_SCRIPT:
                        inScript = !backTag;
                        break;

                    default:
                        break;
                }

                inTag = false;
                continue;
            }

            if (inScript || inTag) {
                docIndex++;
                continue;
            }

            wordIndex = docIndex;

            while (docIndex < docLength && isIndexable(doc.charAt(docIndex))){
                docIndex++;
            }

            builder.append(doc.substring(wordIndex, docIndex).toLowerCase()).append(' ');
            boolean flagged = false;
            if (bold) {
                builder.append('B');
                flagged = true;
            }

            if (header) {
                builder.append('H');
                flagged = true;
            }

            if (italic) {
                builder.append('I');
                flagged = true;
            }

            if (title) {
                builder.append('T');
                flagged = true;
            }

            if (!flagged) {
                builder.append('P');
            }
            builder.append(" " + wordIndex);
            builder.append('\n');
        }
    }
}

