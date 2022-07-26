grammar RouteCatalogRule;

//Configuration values

//Primitive values
String: [A-Za-z]+;
Integer: [0-9]+;
Double: [0-9]+.[0.9]+;
PrimitiveValue: String|Integer|Double;

//Operators
BinaryLogicalOperator: 'AND'|'OR';
UnaryLogicalOperator: 'IS'|'IS NOT';
ComparisonOperator: '=='|'>'|'>='|'<'|'<='|'!=';

VIA: 'VIA'[0-9];
Attribute: 'POL'|'POD'|VIA;
Value:PrimitiveValue;

//Expressions
expression: Attribute UnaryLogicalOperator? ComparisonOperator Value;

//Composite expressions
compositeExpression: BinaryLogicalOperator expression;

//Actions
FilteringAction: 'Enable'|'Disable'|'Replace';

//Rules
filteringRule: FilteringAction expression compositeExpression*;
