package org.softwarefm.helloannotations.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

                    
public class MyPartitionScanner extends RuleBasedPartitionScanner {

    //string constants for different partition types
    public final static String MULTILINE_COMMENT= "multiline_comment";
    public final static String SINGLELINE_COMMENT= "singleline_comment";
    public final static String STRING ="string";
    public final static String[] PARTITION_TYPES = new String[] {MULTILINE_COMMENT, 
        SINGLELINE_COMMENT, STRING};

    /**
     * Creates the partitioner and sets up the appropriate rules.
     */
    public MyPartitionScanner() {
        super();


        IToken multilinecomment= new Token(MULTILINE_COMMENT);
        IToken singlelinecomment= new Token(SINGLELINE_COMMENT);
        IToken string = new Token(STRING);

        List rules= new ArrayList();

        // Add rule for single line comments.
        rules.add(new EndOfLineRule("//", singlelinecomment)); 

        // Add rule for strings and character constants.
        rules.add(new SingleLineRule("\"", "\"", string, '\\'));
        rules.add(new SingleLineRule("'", "'", string, '\\')); 


        // Add rules for multi-line comments and javadoc.
        rules.add(new MultiLineRule("/*", "*/", multilinecomment, (char) 0, true)); 

        IPredicateRule[] result= new IPredicateRule[rules.size()];
        rules.toArray(result);
        setPredicateRules(result);
    }
}