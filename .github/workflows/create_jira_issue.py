import os
import requests
from requests.auth import HTTPBasicAuth
import json


JIRA_DOMAIN = os.environ["JIRA_DOMAIN"]
JIRA_EMAIL = os.environ["JIRA_EMAIL"]
JIRA_API_TOKEN = os.environ["JIRA_API_TOKEN"]
JIRA_PROJECT_KEY = os.environ["JIRA_PROJECT_KEY"]
ISSUE_TITLE = os.environ["ISSUE_TITLE"]
ISSUE_BODY = os.environ["ISSUE_BODY"]

url = f"https://{JIRA_DOMAIN}.atlassian.net/rest/api/3/issue/bulk"
auth = HTTPBasicAuth(JIRA_EMAIL, JIRA_API_TOKEN)
headers = {"Accept": "application/json", "Content-Type": "application/json"}

titles = [line.strip() for line in ISSUE_BODY.splitlines() if "- [ ]" in line]
issues = []
for title in titles:
    summary = title.strip("- [ ]").strip()
    issues.append(
        {
            "fields": {
                "project": {"key": JIRA_PROJECT_KEY},
                "summary": summary,
                "description": {
                    "content": [
                        {
                            "content": [
                                {
                                    "text": "From Github issue: " + ISSUE_TITLE,
                                    "type": "text",
                                }
                            ],
                            "type": "paragraph",
                        }
                    ],
                    "type": "doc",
                    "version": 1,
                },
                "issuetype": {"name": "Task"},
            }
        }
    )

payload = json.dumps({"issueUpdates": issues})

response = requests.request("POST", url, data=payload, headers=headers, auth=auth)
log = json.loads(response.text)
print(log)