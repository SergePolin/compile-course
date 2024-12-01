.class public Main
.super java/lang/Object

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static factorial(I)I
    .limit stack 3
    .limit locals 1
    
    ; Check if n == 0
    iload_0
    ifne RecursiveCase
    
    ; Base case: return 1
    iconst_1
    ireturn
    
    ; Recursive case: return n * factorial(n-1)
    RecursiveCase:
        iload_0
        iload_0
        iconst_1
        isub
        invokestatic Main/factorial(I)I
        imul
        ireturn
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 2
    .limit locals 2

    ; Call factorial(5)
    iconst_5
    invokestatic Main/factorial(I)I
    istore_1

    ; Print result
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload_1
    invokevirtual java/io/PrintStream/println(I)V

    return
.end method
