program Factorial;
function Factorial(n: integer): integer;
begin
    if n = 0 then
        Factorial := 1
    else
        Factorial := n * Factorial(n - 1);
end;

var
    result: integer;
begin
    result := Factorial(5);
    writeln('Factorial: ', result);  // Should print 120
end.
