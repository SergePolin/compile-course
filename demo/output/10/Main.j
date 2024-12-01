.class public Main
.super java/lang/Object

.field static john LPerson;

.method public static main([Ljava/lang/String;)V
    .limit stack 3
    .limit locals 1

    ; Initialize the john instance
    new Person
    dup
    invokespecial Person/<init>()V
    putstatic Main/john LPerson;

    ; Set john.name to "John"
    getstatic Main/john LPerson;
    ldc "John"
    putfield Person/name Ljava/lang/String;

    ; Set john.age to 30
    getstatic Main/john LPerson;
    bipush 30
    putfield Person/age I

    ; Print john.name
    getstatic Main/john LPerson;
    getfield Person/name Ljava/lang/String;
    getstatic java/lang/System/out Ljava/io/PrintStream;
    swap
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V

    ; Print john.age
    getstatic Main/john LPerson;
    getfield Person/age I
    getstatic java/lang/System/out Ljava/io/PrintStream;
    swap
    invokevirtual java/io/PrintStream/println(I)V

    return
.end method
