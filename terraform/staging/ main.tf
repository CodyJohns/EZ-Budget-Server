variable "tenancy_ocid" {}
# variable "user_ocid" {}
# variable "key_fingerprint" {}
# variable "private_key_path" {}
# variable "oci_region" {}
variable "compartment_ocid" {}

provider "oci" {
  # tenancy_ocid     = var.tenancy_ocid
  # user_ocid        = var.user_ocid
  # private_key_path = var.private_key_path
  # fingerprint      = var.key_fingerprint
  # region           = var.oci_region
}

terraform {
  required_providers {
    oci = {
      source  = "oracle/oci"
      version = ">= 4.107.0"
    }
  }
}

########################
# Variables
########################

variable "subnet_ocid" {
  description = "Subnet OCID for the container instance VNIC"
  type        = string
}

variable "availability_domain" {
  description = "Availability Domain name (from OCI console)"
  type        = string
}

variable "image_url" {
  description = "Docker image URL to run"
  type        = string
  default     = "docker.western-solutions.dev/ezbudget_server:staging"
}

variable "registry_username" {
  description = "Username for private Docker registry docker.western-solutions.dev"
  type        = string
  sensitive   = true
}

variable "registry_password" {
  description = "Password for private Docker registry docker.western-solutions.dev"
  type        = string
  sensitive   = true
}

variable "environment_variables" {
  description = "Environment variables to inject into the container"
  type        = map(string)
  default     = {}
}

data "oci_identity_availability_domains" "local_ads" {
  compartment_id = var.tenancy_ocid
}

########################
# Container Instance
########################

resource "oci_container_instances_container_instance" "ezbudget_server" {
  compartment_id           = var.compartment_ocid
  availability_domain      = data.oci_identity_availability_domains.local_ads.availability_domains.0.name
  display_name             = "ezbudget-server-staging"
  container_restart_policy = "ALWAYS"
  shape                    = "CI.Standard.A1.Flex"

  shape_config {
    ocpus         = 1
    memory_in_gbs = 2
  }

  vnics {
    subnet_id             = var.subnet_ocid
    is_public_ip_assigned = true
    nsg_ids               = []
  }

  containers {
    display_name = "ezbudget_server_staging"
    image_url    = var.image_url

    environment_variables = var.environment_variables

    # If you need to override CMD, you can add:
    # command = ["java", "-jar", "/app/server.jar"]
  }

  image_pull_secrets {
    registry_endpoint = "docker.western-solutions.dev"
    secret_type       = "BASIC"
    username          = base64encode(var.registry_username)
    password          = base64encode(var.registry_password)
  }
}
