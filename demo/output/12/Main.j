.class public Main
.super java/lang/Object

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 3
    .limit locals 4
    
    ldc "John"
    astore_1
    
    ldc "Doe" 
    astore_2
    
    new java/lang/StringBuilder
    dup
    aload_1
    invokestatic java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
    invokespecial java/lang/StringBuilder/<init>(Ljava/lang/String;)V
    ldc " "
    invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    aload_2
    invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;
    astore_3
    
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_3
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    
    return
.end method
