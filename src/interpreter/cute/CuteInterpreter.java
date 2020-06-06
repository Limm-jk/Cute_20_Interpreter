package interpreter.cute;

import java.io.File;

import lexer.TokenType;
import parser.parse.*;
import parser.ast.*;
import java.util.Scanner;

public class CuteInterpreter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		ClassLoader cloader = ParserMain.class.getClassLoader();
//		File file = new File(
//			cloader.getResource("interpreter/as07.txt").getFile()
//		);
//      String str = new String("(+ 3 2 )");
		Scanner sc = new Scanner(System.in);
		System.out.println("-1�� �Է��ϸ� ����");
        while(true){
			System.out.print("$ ");
			String input = sc.nextLine();

			CuteParser cuteParser = new CuteParser(input);
			CuteInterpreter interpreter = new CuteInterpreter();
			Node parseTree = cuteParser.parseExpr();
			Node resultNode = interpreter.runExpr(parseTree);
			NodePrinter nodePrinter = new NodePrinter(resultNode);
			nodePrinter.prettyPrint();
		}
	}



	private void errorLog(String err) {
		System.out.println(err);
	}

	private Node runExpr(Node rootExpr) {
		// TODO Auto-generated method stub
		if (rootExpr == null)  return null;
		if (rootExpr instanceof IdNode) return rootExpr;
		else if (rootExpr instanceof IntNode) return rootExpr;
		else if (rootExpr instanceof BooleanNode) return rootExpr;
		else if (rootExpr instanceof ListNode) return runList((ListNode) rootExpr);
		else errorLog("run Expr error");
		return null;
	}

	private Node runList(ListNode list) {
		// TODO Auto-generated method stub
		list = (ListNode)stripList(list);
		if (list.equals(ListNode.EMPTYLIST)) return list;
		if (list.car() instanceof FunctionNode) {
			return runFunction((FunctionNode) list.car(), list.cdr());
		}
		if (list.car() instanceof BinaryOpNode) {
			return runBinary(list);
		}
		return list;
	}

	private Node runFunction(FunctionNode operator, ListNode operand) {
		// TODO Auto-generated method stub
		if (operand == null) return null;
		switch(operator.funcType) {
			case CDR:
				Node checked_node = runExpr(operand);//���ο� function�̳� �����ڰ� �ִ��� üũ 
				if (((ListNode)checked_node).car() instanceof QuoteNode) {
					checked_node = ((ListNode)checked_node).cdr();//'�� �� �׳ɰ�
				}
				return ((ListNode) checked_node).cdr();
			case CAR:
				Node checked_node_car = runExpr(operand);//���ο� function�̳� �����ڰ� �ִ��� üũ 
				if (((ListNode)checked_node_car).car() instanceof QuoteNode) {
					checked_node_car = ((ListNode)checked_node_car).cdr();//'�� �� �׳ɰ�
				}
				return ((ListNode) checked_node_car).car();
//			case CONS:
//			case DEFINE:
//			case LAMBDA:
//			case COND:
//			case NOT:
//			case EQ_Q:
//			case NULL_Q:
//			case ATOM_Q:
			default:
				break;
		}
		return null;
	}
	
	private Node runBinary(ListNode list) {
		// TODO Auto-generated method stub
		Node Num1 = runExpr(list.cdr().car());//list.car�� ������ �״� ù ���Ұ� ù ����
		Node Num2 = runExpr(list.cdr().cdr().car());
		BinaryOpNode operator = (BinaryOpNode) list.car();
		
		switch(operator.binType) {
			case MINUS:
				return new IntNode(Integer.toString(((IntNode) Num1).getValue()-((IntNode) Num2).getValue()));
			case PLUS:
				return new IntNode(Integer.toString(((IntNode) Num1).getValue()+((IntNode) Num2).getValue()));
			case TIMES:
				return new IntNode(Integer.toString(((IntNode) Num1).getValue()*((IntNode) Num2).getValue()));
			case DIV:
				return new IntNode(Integer.toString(((IntNode) Num1).getValue()/((IntNode) Num2).getValue()));
				// Num1�� Num2�� value���� ���� ������ ��Ʈ������ �� ��忡 �־��� - ��Ģ����
			case LT:
				if (((IntNode) Num1).getValue() < ((IntNode) Num2).getValue()) return BooleanNode.TRUE_NODE;
				else return BooleanNode.FALSE_NODE;
			case GT:
				if (((IntNode) Num1).getValue() > ((IntNode) Num2).getValue()) return BooleanNode.TRUE_NODE;
				else return BooleanNode.FALSE_NODE;
			case EQ:
				if (((IntNode) Num1).getValue() == ((IntNode) Num2).getValue()) return BooleanNode.TRUE_NODE;
				else return BooleanNode.FALSE_NODE;
			default:
				break;
		}
		return null;
	}

	

	private Node stripList(ListNode node) {
		// TODO Auto-generated method stub
		if (node.car() instanceof ListNode && node.cdr().car() == null) {
			Node listNode = node.car();
			return listNode;
		}
		else return node;
	}


}
