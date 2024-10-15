package src;

import java.util.List;

// Base class for all expression nodes
public abstract class ASTNodes {
}

class Program extends ASTNodes {
    List<Declaration> declarations;

    public Program(List<Declaration> declarations) {
        this.declarations = declarations;
    }

    @Override
    public String toString() {
        return "Program{" + "declarations=" + declarations + '}';
    }
}

interface Declaration {
}

class RoutineDeclaration implements Declaration {
    String name;
    List<Parameter> parameters;
    List<Statement> body;

    public RoutineDeclaration(String name, List<Parameter> parameters, List<Statement> body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public String toString() {
        return "RoutineDeclaration{" + "name='" + name + "', parameters=" + parameters + ", body=" + body + '}';
    }
}

class TypeDeclaration implements Declaration {
    String name;
    VarDeclaration fields;

    public TypeDeclaration(String name, VarDeclaration fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "TypeDeclaration{" + "name='" + name + "', fields=" + fields + '}';
    }
}

class VarDeclaration extends Statement implements Declaration {
    String name;
    Type type;
    Expression initialValue;

    public VarDeclaration(String name, Type type, Expression initialValue) {
        this.name = name;
        this.type = type;
        this.initialValue = initialValue;
    }

    @Override
    public String toString() {
        return "VarDeclaration{" + "name='" + name + "', type=" + type + ", initialValue=" + initialValue + '}';
    }
}

class Parameter extends ASTNodes {
    String name;
    Type type;

    public Parameter(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Parameter{" + "name='" + name + "', type=" + type + '}';
    }
}

abstract class Type extends ASTNodes {
}

class SimpleType extends Type {
    String name;

    public SimpleType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SimpleType{" + "name='" + name + "'}";
    }
}

class ArrayType extends Type {
    Type elementType;
    int size;

    public ArrayType(Type elementType, int size) {
        this.elementType = elementType;
        this.size = size;
    }

    @Override
    public String toString() {
        return "ArrayType{" + "elementType=" + elementType + ", size=" + size + '}';
    }
}

abstract class Statement extends ASTNodes {
}

class AssignmentStatement extends Statement {
    String id;
    Expression expression;

    public AssignmentStatement(String id, Expression expression) {
        this.id = id;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "AssignmentStatement{" + "id='" + id + "', expression=" + expression + '}';
    }
}

class IfStatement extends Statement {
    Expression condition;
    List<Statement> thenBody;
    List<Statement> elseBody;

    public IfStatement(Expression condition, List<Statement> thenBody, List<Statement> elseBody) {
        this.condition = condition;
        this.thenBody = thenBody;
        this.elseBody = elseBody;
    }

    @Override
    public String toString() {
        return "IfStatement{" + "condition=" + condition + ", thenBody=" + thenBody + ", elseBody=" + elseBody + '}';
    }
}

class WhileStatement extends Statement {
    Expression condition;
    List<Statement> body;

    public WhileStatement(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "WhileStatement{" + "condition=" + condition + ", body=" + body + '}';
    }
}

class ForStatement extends Statement {
    String id;
    Expression start;
    Expression end;
    List<Statement> body;
    boolean reverse;

    public ForStatement(String id, Expression start, Expression end, List<Statement> body, boolean reverse) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.body = body;
        this.reverse = reverse;
    }

    @Override
    public String toString() {
        return "ForStatement{" + "id='" + id + "', start=" + start + ", end=" + end + ", body=" + body + ", reverse="
                + reverse + '}';
    }
}

class PrintStatement extends Statement {
    Expression expression;

    public PrintStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "PrintStatement{" + "expression=" + expression + '}';
    }
}

class ReadStatement extends Statement {
    String id;

    public ReadStatement(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ReadStatement{" + "id='" + id + "'}";
    }
}

class ReturnStatement extends Statement {
    Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "ReturnStatement{" + "expression=" + expression + '}';
    }
}

abstract class Expression extends ASTNodes {
}

class IntegerLiteral extends Expression {
    int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "IntegerLiteral{" + "value=" + value + '}';
    }
}

class RealLiteral extends Expression {
    double value;

    public RealLiteral(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RealLiteral{" + "value=" + value + '}';
    }
}

class StringLiteral extends Expression {
    String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "StringLiteral{" + "value='" + value + "'}";
    }
}

class BooleanLiteral extends Expression {
    boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BooleanLiteral{" + "value=" + value + '}';
    }
}

class IdentifierExpression extends Expression {
    String name;

    public IdentifierExpression(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "IdentifierExpression{" + "name='" + name + "'}";
    }
}

class BinaryExpression extends Expression {
    Expression left;
    BinaryOp operator;
    Expression right;

    public BinaryExpression(Expression left, BinaryOp operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return "BinaryExpression{" + "left=" + left + ", operator=" + operator + ", right=" + right + '}';
    }
}

enum BinaryOp {
    PLUS, MINUS, TIMES, DIVIDE, MODULO, EQUAL, NOT_EQUAL, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL, AND, OR, XOR
}

class UnaryExpression extends Expression {
    UnaryOp operator;
    Expression operand;

    public UnaryExpression(UnaryOp operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public String toString() {
        return "UnaryExpression{" + "operator=" + operator + ", operand=" + operand + '}';
    }
}

enum UnaryOp {
    NOT, MINUS
}

class RoutineCall extends Expression {
    String name;
    List<Expression> arguments;

    public RoutineCall(String name, List<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "RoutineCall{" + "name='" + name + "', arguments=" + arguments + '}';
    }
}

class RecordAccess extends Expression {
    Expression record;
    String field;

    public RecordAccess(Expression record, String field) {
        this.record = record;
        this.field = field;
    }

    @Override
    public String toString() {
        return "RecordAccess{" + "record=" + record + ", field='" + field + "'}";
    }
}

class ArrayAccess extends Expression {
    Expression array;
    Expression index;

    public ArrayAccess(Expression array, Expression index) {
        this.array = array;
        this.index = index;
    }

    @Override
    public String toString() {
        return "ArrayAccess{" + "array=" + array + ", index=" + index + '}';
    }
}

class ExpressionStatement extends Statement implements Declaration {
    Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "ExpressionStatement{" + "expression=" + expression + '}';
    }
}
