program ArrayExample;
var
    numbers: array[1..5] of integer;
    i: integer;
begin
    numbers[1] := 10;
    numbers[2] := 20;
    numbers[3] := 30;
    numbers[4] := 40;
    numbers[5] := 50;

    for i := 1 to 5 do
        writeln('Number ', i, ': ', numbers[i]);
end.
