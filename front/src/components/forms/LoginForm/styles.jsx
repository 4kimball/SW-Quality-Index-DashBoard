import styled from "styled-components";
import { fonts, colors } from "../../../styles";

export const StyledForm = styled.form`
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
`;

export const InputBox = styled.div`
  display: flex;
  flex-direction: column;
  align-items: ${(props) => (props.isEnd ? "flex-end" : "flex-start")};
`;

export const StyledInput = styled.input`
  width: 400px;
  height: 40px;
  border: none;
  border-bottom: 1px solid ${colors.navy};
  color: ${colors.navy};
  font-size: ${fonts.md};
  padding: 1rem;
`;

export const ErrorMessage = styled.div`
  height: 40px;
  color: ${colors.red};
  font-size: ${fonts.sm};
  padding: 0.5rem;
`;

export const StyledButton = styled.button`
  background-color: ${colors.navy};
  color: ${colors.white};
  width: 400px;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
`;
