.class public Main
.super java/lang/Object

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main()V
    .limit stack 20
    .limit locals 5

    ; var firstName: string
    ldc "John"

    ; var lastName: string
    ldc "Doe"

    ; var fullName: string
    ldc " "
    iadd
    iadd

    getstatic java/lang/System/out Ljava/io/PrintStream;
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V

    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 4
    .limit locals 20

    ; var firstName: string
    ldc "John"

    ; var lastName: string
    ldc "Doe"

    ; var fullName: string
    ldc " "
    iadd
    iadd

    getstatic java/lang/System/out Ljava/io/PrintStream;
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V


    return
.end method
