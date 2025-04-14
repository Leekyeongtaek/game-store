package com.mrlee.game_store.practice;

import lombok.Data;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class HashTest {

    // equals()와 hashCode()를 재정의하지 않으면, 동일한 Id를 가진 상품이라도 HashSet 에 중복 저장될 수 있다.
    //- hashCode()는 해시 기반 자료구조에서 버킷 인덱스를 계산할 때 사용되며,
    //- equals()는 해시 충돌이 발생했을 때, 같은 버킷 안의 객체들과 논리적으로 동일한지 비교할 때 사용된다.
    @Test
    void hashNoOverrideTest() {
        Set<Product1> hashSet = new HashSet<>();
        Product1 product1 = new Product1(1L, "신라면");
        Product1 product2 = new Product1(1L, "신라면");
        System.out.println(Objects.hashCode(product1));
        System.out.println(System.identityHashCode(product1));
        System.out.println(System.identityHashCode(product2));
        hashSet.add(product1);
        hashSet.add(product2);
        Assertions.assertThat(hashSet.size()).isEqualTo(2);
    }

    //상품을 HashSet 자료구조를 통해 입력받는다고 가정.
    //상품은 Id를 기준으로 구분하고 중복 등록이 발생하면 안되는 상황이라고 가정.
    //상품을 두 개 저장했지만, id가 중복되기 때문에 해시셋 사이즈는 1이 나온다.
    @Test
    void hashRealExampleTest() {
        Set<Product2> hashSet = new HashSet<>();
        Product2 product1 = new Product2(1L, "신라면");
        Product2 product2 = new Product2(1L, "신라면");
        hashSet.add(product1);
        hashSet.add(product2);
        Assertions.assertThat(hashSet.size()).isEqualTo(1);
    }

    static class Product1 {
        private Long id;
        private String name;

        public Product1(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static class Product2 {
        private Long id;
        private String name;

        public Product2(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        //해시 충돌이 발생했을 때 대상 객체와 논리적으로 같은지 판단할 때 사용
        //파라미터에 존재하는 o는 누구인가? -> 비교 대상 객체
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Product2 product2 = (Product2) o;
            return Objects.equals(id, product2.id);
        }

        //해시 버킷 인덱스 결정할 때 사용
        //hashCode 메서드는 재정의 하지 않으면 객체의 참조값을 기반으로 해시 코드를 생성
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
