import type {
  ApplicationInput,
  ApplicationListResponse,
  CreateApplicationResponse,
  LoanApplication
} from "./types";

async function readJson<T>(response: Response): Promise<T> {
  return (await response.json()) as T;
}

export async function createApplication(input: ApplicationInput): Promise<CreateApplicationResponse> {
  const response = await fetch("/api/applications", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(input)
  });
  return readJson<CreateApplicationResponse>(response);
}

export async function getApplications(): Promise<ApplicationListResponse> {
  const response = await fetch("/api/applications");
  return readJson<ApplicationListResponse>(response);
}

export async function getApplication(id: number): Promise<{ loanApplication?: LoanApplication; error?: string }> {
  const response = await fetch(`/api/applications/${id}`);
  return readJson<{ loanApplication?: LoanApplication; error?: string }>(response);
}
