.class public Main
.super java/lang/Object

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 2
    .limit locals 2

    bipush 10
    istore_1

    iload_1
    ifle NonPositive
    
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Positive"
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto End

NonPositive:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Non positive" 
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V

End:
    return

.end method
