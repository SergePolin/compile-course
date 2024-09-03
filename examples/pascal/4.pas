program IfElseCondition;
var
    num: integer;
begin
    num := -10;
    if num > 0 then
        writeln('Positive')
    else if num < 0 then
        writeln('Negative')
    else
        writeln('Zero');
end.
