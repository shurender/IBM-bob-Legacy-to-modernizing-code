export type ApplicationInput = {
  customerName: string;
  age: string;
  employmentType: string;
  monthlyIncome: string;
  creditScore: string;
  existingEmi: string;
  loanAmount: string;
  loanTenure: string;
};

export type ValidationResult = {
  valid: boolean;
  errors: string[];
  warnings: string[];
};

export type LoanApplication = {
  id: number;
  applicationReference: string;
  customerName: string;
  age: number;
  monthlyIncome: number;
  employmentType: string;
  creditScore: number;
  existingEmi: number;
  loanAmount: number;
  loanTenure: number;
  decision: string;
  decisionCode: string;
  decisionReason: string;
  createdAt: string;
};

export type CreateApplicationResponse = {
  loanApplication?: LoanApplication;
  validationResult?: ValidationResult;
  error?: string;
};

export type ApplicationListResponse = {
  applicationList: LoanApplication[];
  totalCount: number;
  error?: string;
};
