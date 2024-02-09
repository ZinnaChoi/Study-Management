import React from "react";
import CommonDialog from "../../components/CommonDialog";
import { Button, TextField } from "@mui/material";
import "../../styles/Button.css";

// 부재 일정 화면
export default function MembersInfo() {
  // prop 전달을 위한 변수
  const dialogProps = {
    btnTitle: "다이얼로그 버튼 이름",
    title: "다이얼로그 제목",
    names: ["1st Input", "2nd Input"],
    descriptions: ["Input 설명1", "Input 설명2"],
    isRequireds: [true, false],
    inputTypes: ["email", ""],
    acceptStr: "확인",
    cancleStr: "취소",
    submitEvt: doSomeAction,
    showButton: true,
  };
  return (
    <React.Fragment>
      <h1>스터디원 정보 조회 화면 - 홈(메인) 화면</h1>
      <button>구현 시 기상 성공 버튼 추가 필요!!</button>
      <hr />
      <div>
        {/* 필요없는 props 경우는 전달하지 않아도 됨 */}
        <CommonDialog
          btnTitle={dialogProps.btnTitle} // 버튼 이름(필수)
          title={dialogProps.title} // 팝업 이름(필수)
          names={dialogProps.names} // Input 이름(필수)
          descriptions={dialogProps.descriptions} // 필드 설명
          isRequireds={dialogProps.isRequireds} // 필드 validation 여부(boolean)
          inputTypes={dialogProps.inputTypes} // html input type
          acceptStr={dialogProps.acceptStr} // 확인 버튼 str
          cancleStr={dialogProps.cancleStr} // 취소 버튼 str
          submitEvt={dialogProps.submitEvt} // 확인 버튼 클릭 시 함수 실행
          showButton={dialogProps.showButton}
          extraComponents={<ExtraComponents />} // 추가적인 컴포넌트 연결(테이블 등)
        ></CommonDialog>
      </div>
    </React.Fragment>
  );
}

const ExtraComponents = () => {
  const printOne = () => {
    console.log("One!");
  };
  const printTwo = () => {
    console.log("Two!");
  };

  return (
    <div>
      <h3>추가 컴포넌트</h3>
      {/* 추가 컴포넌트의 경우 name attribute(key로 사용됨)가 있어야 함수 호출 시 값을 받을 수 있음 */}
      <TextField
        name="ssd"
        variant="outlined"
        required
        sx={{
          "& .MuiInputBase-root": {
            height: 32,
          },
        }}
      ></TextField>
      <Button className="accept-btn" type="button" onClick={printOne}>
        btn 1
      </Button>
      <Button className="cancel-btn" type="button" onClick={printTwo}>
        btn 2
      </Button>
    </div>
  );
};

// 팝업내 확인 버튼 누르면 해당 함수 호출
const doSomeAction = (data) => {
  alert(JSON.stringify(data));
};
