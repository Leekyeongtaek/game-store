package com.mrlee.game_store.practice;

import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PolymorphismTest {

    @Test
    void 다형성과_OCP원칙_준수_테스트() {
        List<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        List<Integer> linkedlist = new LinkedList<>();
        linkedlist.add(1);
        List<String> stringList = new ArrayList<>();
        stringList.add("A");

        printList(arrayList);
        printList(linkedlist);
        printList(stringList);
    }

    //제네릭 와일드 카드를 사용해서 어떤 타입의 제네릭이든 받을 수 있다.
    //상위 타입으로 하위 객체를 참조하는 방식은 '다형적 참조'라고 한다
    //OCP 원칙을 준수: 코드 수정 없이 구현 클래스 변경 가능, 구체 클래스에 의존하지 않음
    private void printList(List<?> list) {
        int size = list.size();
        System.out.println("리스트의 사이즈 = " + size);
    }

    @Test
    void 다형적_참조의_장점과_한계() {
        List<Integer> arrayList = new ArrayList<>();

        // 참조 타입이 List이기 때문에 인터페이스(List)에 정의된 메서드만 호출 가능
        // ArrayList 고유 메서드를 사용할 수 없다.
        //다형적 참조는 유연성을 제공하지만, 구현 클래스 고유 기능에는 접근할 수 없는 제한이 있다.
        // arrayList.ensureCapacity(); // 컴파일 에러

        //구체 클래스 타입 선언: 다형성(polymorphism)이 '필요하지 않을 때' 또는 '구현체 고유 기능을 써야 할 때'
        ArrayList<Integer> list = new ArrayList<>();
        list.ensureCapacity(100); //구체 타입의 메서드 호출 가능
    }

    @Data
    static class MyPoly {

        private List<LittlePoly> polyList;

        //이 구조는 느슨한 결합(loose coupling) 의 대표적인 예
        //클래스 내부는 List라는 상위 타입(인터페이스)만 알고 있고, 구체 구현체는 전혀 알지 못하므로 유연한 설계가 가능하다
        public MyPoly(List<LittlePoly> polyList) {
            this.polyList = polyList;
        }
    }

    @Data
    static class LittlePoly {
        private String name;
        private int age;

        public LittlePoly(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    interface MyList {
        int size();

        boolean isEmpty();
    }

    @Data
    static class MyParent {

        private String name;
        private int parentAge;

        public MyParent() {
        }

        public MyParent(String name) {
            this.name = name;
        }

        public void printParent() {
            System.out.println("MyParent.printParent()");
        }

        public void print() {
            System.out.println("MyParent.print()");
        }
    }

    @Data
    static class MyArrayList extends MyParent implements MyList {

        private String name;
        private int arrayListCnt;

        public MyArrayList(String name, String parentName) {
            super(parentName);
            this.name = name;
        }

        @Override
        public int size() {
            System.out.println("MyArrayList.size()");
            return 0;
        }

        @Override
        public boolean isEmpty() {
            System.out.println("MyArrayList.isEmpty()");
            return false;
        }

        @Override
        public void print() {
            System.out.println("MyArrayList.print");
        }

        public void myArrayListCall() {
            System.out.println("arrayList 전용 메서드 호출");
        }
    }

    @Data
    static class MyLinkedList extends MyParent implements MyList {

        private String name;
        private int MyLinkedListCnt;

        public MyLinkedList(String parentName, String name) {
            super(parentName);
            this.name = name;
        }

        @Override
        public int size() {
            System.out.println("MyLinkedList.size()");
            return 0;
        }

        @Override
        public boolean isEmpty() {
            System.out.println("MyLinkedList.isEmpty()");
            return false;
        }

        @Override
        public void print() {
            System.out.println("MyLinkedList.print()");
        }

        public void myLinkedListCall() {
            System.out.println("linkedList 전용 메서드 호출");
        }
    }

    static class A {

    }

    static class B  extends A {

    }

    static class C extends B {

    }
}
