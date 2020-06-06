package parser.parse;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import parser.ast.*;

public class NodePrinter {
    private final String OUTPUT_FILENAME = "output06.txt";
    private StringBuffer sb = new StringBuffer();
    private Node root;

    public NodePrinter(Node root) {
        this.root = root;
    }
 // ListNode, ‘ Node, Node에 대한 printNode 함수를 각각 overload 형식으로 작성
    private void printList(ListNode li) {
        if (li == ListNode.EMPTYLIST) {
            sb.append("( )");
            return;
        }
        if(li == ListNode.ENDLIST)
        	return;
        printNode(li.car());
        if(li.cdr() != null) printList(li.cdr());
    }

    private void printNode(Node node) {
        if (node == null)
            return;

        if (node instanceof ListNode) {
            ListNode ln = (ListNode) node;
            
            if (ln.car() instanceof QuoteNode) {
            	//To Do
            	sb.append("\'");
            	printNode(ln.cdr());
            }
            else {
            	if ((ln.car() instanceof ListNode) && ((ListNode)(ln.car())).car() instanceof QuoteNode){
            		printList(ln);
            	}
            	else if(ln.cdr().car() == null)
            		printNode(ln.car());
            	else {
	            	sb.append("( ");
	            	printList(ln);
	            	sb.append(" ) ");
            	}	
            }
        } 
        
        else sb.append("[" + node + "] ");
    }

    public void prettyPrint() {
        printNode(root);
        System.out.print("...");
        System.out.println(sb.toString());

//        try (FileWriter fw = new FileWriter(OUTPUT_FILENAME);
//             PrintWriter pw = new PrintWriter(fw)) {
//            pw.write(sb.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
