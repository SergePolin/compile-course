program MultiplicationTable;
var
    i, j: integer;
begin
    for i := 1 to 5 do
    begin
        for j := 1 to 5 do
            writeln(i, ' * ', j, ' = ', i * j);
    end;
end.
