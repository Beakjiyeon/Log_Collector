/*
    hateoas 개념 및 샘플코드 테스트 패키지
*/

## HATEOAS(Hypermedia As The Engine Of Application State)
### - REST API 의 최고급 구현
### - REST Api 를 사용하는 클라이언트가 전적으로 서버와 동적인 상호작용이 가능하도록 하는 것
### - 서버가 요청에 필요한 URI 를 응답에 포함시켜 반환
### - ex. 사용자 정보를 생성(POST)하는 요청에 대한 응답 : 조회, 수정, 삭제할 때의 모든 동작을 URI 를 이용해 동적으로 알려줌

## 장점
### - 요청 URI 가 변경되더라도 클라이언트에서 동적으로 생성된 URI 를 사용함으로써, 클라이언트가 URI 수정에 따른 코드를 변경하지 않아도 되는 편리함을 제공
### - URI 정보를 통해 들어오는 요청을 예측할 수 있게 됩니다.
### - Resource 가 포함된 URI 를 보여주기 때문에, Resource 에 대한 신뢰를 얻을 수 있습니다.
### - 클라이언트가 제공되는 API 의 변화에 일일이 대응하지 않아도 되는 편리함을 얻을 수 있습니다.
