program SimpleCalculator;
var
    a, b, result: integer;
    op: char;
begin
    writeln('Enter first number:');
    readln(a);
    writeln('Enter second number:');
    readln(b);
    writeln('Enter operator (+, -, *, /):');
    readln(op);

    case op of
        '+': result := a + b;
        '-': result := a - b;
        '*': result := a * b;
        '/': result := a div b;
    else
        writeln('Invalid operator');
    end;

    writeln('Result: ', result);
end.
