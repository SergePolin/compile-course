program BooleanLogic;
var
    a, b: boolean;
begin
    a := true;
    b := false;
    writeln('a and b: ', a and b);  // Should print false
    writeln('a or b: ', a or b);    // Should print true
    writeln('not a: ', not a);      // Should print false
end.
