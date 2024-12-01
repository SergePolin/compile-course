.class public Main
.super java/lang/Object

.field private static numbers [I

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 3
    .limit locals 2

    ; Initialize array
    iconst_5
    newarray int
    putstatic Main/numbers [I

    ; Assign values to array
    getstatic Main/numbers [I
    iconst_0
    bipush 10
    iastore

    getstatic Main/numbers [I
    iconst_1  
    bipush 20
    iastore

    getstatic Main/numbers [I
    iconst_2
    bipush 30
    iastore

    getstatic Main/numbers [I
    iconst_3
    bipush 40
    iastore

    getstatic Main/numbers [I
    iconst_4
    bipush 50
    iastore

    ; Initialize loop counter
    iconst_0
    istore_1

    ; Start of loop
loop:
    iload_1
    iconst_5
    if_icmpge end_loop

    ; Print array element
    getstatic java/lang/System/out Ljava/io/PrintStream;
    getstatic Main/numbers [I
    iload_1
    iaload
    invokevirtual java/io/PrintStream/println(I)V

    ; Increment counter
    iload_1
    iconst_1
    iadd
    istore_1
    goto loop

end_loop:
    return
.end method
