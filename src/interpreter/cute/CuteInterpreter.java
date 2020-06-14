package interpreter.cute;


import lexer.TokenType;
import parser.parse.*;
import parser.ast.*;

import java.util.Scanner;

public class CuteInterpreter {

	private Node[] table = new Node[123];

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		ClassLoader cloader = ParserMain.class.getClassLoader();
//		File file = new File(
//			cloader.getResource("interpreter/as07.txt").getFile()
//		);
//      String str = new String("(+ 3 2 )");
		Scanner sc = new Scanner(System.in);
		CuteInterpreter interpreter = new CuteInterpreter();
        while(true){
			System.out.print("$ ");
			String input = sc.nextLine();

			CuteParser cuteParser = new CuteParser(input);
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
		if (rootExpr instanceof IdNode) return lookupTable(((IdNode)rootExpr).toString());
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
				Node checked_node = runExpr(operand);//내부에 function이나 연산자가 있는지 체크

				if (((ListNode)checked_node).car() instanceof IdNode){
					checked_node = runExpr(((ListNode)checked_node).car());
				}
				if (((ListNode)checked_node).car() instanceof QuoteNode) {
					checked_node = ((ListNode)checked_node).cdr();//'를 뺀 그냥거
				}

				return ((ListNode) checked_node).cdr();
			case CAR:
				Node checked_node_car = runExpr(operand);//내부에 function이나 연산자가 있는지 체크

				if (((ListNode)checked_node_car).car() instanceof IdNode){
					checked_node_car = runExpr(((ListNode)checked_node_car).car());
				}
				if (((ListNode)checked_node_car).car() instanceof QuoteNode) {
					checked_node_car = ((ListNode)checked_node_car).cdr();//'를 뺀 그냥거
				}

				return ((ListNode) checked_node_car).car();
			case CONS:
				Node cons_head = runExpr(operand.car());//원소
				Node cons_tail = runExpr(operand.cdr().car());//뒤 리스트

				if (cons_head instanceof ListNode && ((ListNode)cons_head).car() instanceof IdNode){
					cons_head = runExpr(((ListNode)cons_head).car());
				}
				if (((ListNode)cons_tail).car() instanceof IdNode){
					cons_tail = runExpr(((ListNode)cons_tail).car());
				}

				if (cons_head instanceof ListNode && ((ListNode) cons_head).car() instanceof QuoteNode)
					cons_head = runExpr(((ListNode)cons_head).cdr());
				if (((ListNode) cons_tail).car() instanceof QuoteNode)
					cons_tail = runExpr(((ListNode)cons_tail).cdr());

				return ListNode.cons(cons_head, (ListNode)cons_tail);
			case DEFINE:
				Node define_node = runExpr(operand); //리스트 노드 체크
				Node letter = ((ListNode)define_node).car();//list.car은 연산자 그담 첫 원소가 첫 숫자
				Node Num = runExpr(((ListNode)define_node).cdr().car());
				insertTable(((IdNode)letter).toString(), Num);

				return null;
//			case LAMBDA:
			case COND:
				if(runExpr(((ListNode)operand.car()).car()) == BooleanNode.TRUE_NODE){
					if (((ListNode)operand.car()).cdr().car() instanceof IdNode)
						return runExpr(((ListNode)operand.car()).cdr().car());
					return runExpr(((ListNode)operand.car()).cdr());
				}
				else{
					return runFunction(operator, operand.cdr());
				}

			case NOT:
				Node not_node = runExpr(operand);
				if (((ListNode)not_node).car() instanceof IdNode){
					not_node = runExpr(((ListNode)not_node).car());
				}
				if (not_node == BooleanNode.TRUE_NODE)
					return BooleanNode.FALSE_NODE;
				return BooleanNode.TRUE_NODE;
			case EQ_Q:
				Node node1 = runExpr(operand.car());//원소
				Node node2 = runExpr(operand.cdr().car());//뒤 리스트

//				if (node1 instanceof ListNode && ((ListNode)node1).car() instanceof IdNode){
//					node1 = runExpr(((ListNode)node1).car());
//				}
//				if (node2 instanceof ListNode && ((ListNode)node2).car() instanceof IdNode){
//					node2 = runExpr(((ListNode)node2).car());
//				}

				if (node1 instanceof ListNode && ((ListNode) node1).car() instanceof QuoteNode)
					node1 = runExpr(((ListNode)node1).cdr());
				if (((ListNode) node2).car() instanceof QuoteNode)
					node2 = runExpr(((ListNode)node2).cdr());

				if(node1.equals(node2)) return BooleanNode.TRUE_NODE;
				else return BooleanNode.FALSE_NODE;

			case NULL_Q:
				Node null_node = runExpr(operand);

				if (((ListNode)null_node).car() instanceof IdNode){
					null_node = runExpr(((ListNode)null_node).car());
				}
				if (((ListNode) null_node).car() instanceof QuoteNode)
					null_node = runExpr(((ListNode)null_node).cdr());

				if (((ListNode)null_node).car() == null) // 리스트가 null
					return BooleanNode.TRUE_NODE;
				else // 리스트가 null이 아님
					return BooleanNode.FALSE_NODE;
			case ATOM_Q:
				Node atom_node = runExpr(operand); // 노드를 확인

				if (((ListNode)atom_node).car() instanceof IdNode){
					atom_node = runExpr(((ListNode)atom_node).car());
				}
				if (!(((ListNode)atom_node).car() instanceof QuoteNode) && ((ListNode)atom_node).cdr().car() == null){
					return BooleanNode.TRUE_NODE;
				}
				if (((ListNode) atom_node).car() instanceof QuoteNode) // ListNode확인
					atom_node = runExpr(((ListNode)atom_node).cdr());

				if (atom_node instanceof ListNode && atom_node == ListNode.EMPTYLIST)
					return BooleanNode.TRUE_NODE;
				else if (atom_node instanceof ListNode && ((ListNode)atom_node).car() != null)
					return BooleanNode.FALSE_NODE;
				else //그외 리스트가 아니거나 null이거나
					return BooleanNode.TRUE_NODE;
			default:
				break;
		}
		return null;
	}
	
	private Node runBinary(ListNode list) {
		// TODO Auto-generated method stub
		Node Num1 = runExpr(list.cdr().car());//list.car은 연산자 그담 첫 원소가 첫 숫자
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
				// Num1과 Num2의 value끼리 연산 해준후 스트링으로 새 노드에 넣어줌 - 사칙연산
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

	private Node lookupTable(String id) {
		char char_id = id.charAt(0);
		if(table[(int)char_id] == null){
			return new IdNode(id);
		}
		return table[(int)char_id];
	}

	private void insertTable(String id, Node value){
		char char_id = id.charAt(0);
		table[(int)char_id] = value;
	}

}
