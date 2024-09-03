program RecordExample;
type
    Person = record
        name: string;
        age: integer;
    end;

var
    john: Person;
begin
    john.name := 'John';
    john.age := 30;
    writeln('Name: ', john.name);
    writeln('Age: ', john.age);
end.
