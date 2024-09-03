program StringConcat;
var
    firstName, lastName, fullName: string;
begin
    firstName := 'John';
    lastName := 'Doe';
    fullName := firstName + ' ' + lastName;
    writeln('Full Name: ', fullName);  // Should print "John Doe"
end.
