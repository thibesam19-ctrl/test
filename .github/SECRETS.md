# Required GitHub Secrets

Go to: Repository → Settings → Secrets and variables → Actions → New repository secret

| Secret | Description | Required |
|--------|-------------|----------|
| `GOOGLE_SERVICES_JSON` | Full content of your `google-services.json` file | Yes |
| `OPENAI_API_KEY` | OpenAI API key for AI coach | Optional |
| `CLAUDE_API_KEY` | Anthropic API key for AI coach | Optional |
| `GEMINI_API_KEY` | Google Gemini API key for AI coach | Optional |

## How to set GOOGLE_SERVICES_JSON
Copy the entire content of your `app/google-services.json` file and paste it as the secret value.
