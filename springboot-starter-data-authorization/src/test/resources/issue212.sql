SELECT
  id,
  unit_name,
  department_name,
  employee_name,
  sex,
  former_name,
  nationality_code,
  person_nation_code,
  card_type_code,
  card_no,
  card_birth_date,
  present_address,
  bron_place_desc,
  native_place_desc,
  household_type_code,
  household_local,
  political_affiliation_code,
  join_date,
  join_org_name,
  introducer,
  health_status_code,
  marital_status_code,
  whether_disabled,
  staff_speciality,
  sap_number,
  remarks,
  telephone,
  email,
  emergency_contact,
  emergency_contact_relation_code,
  emergency_contact_phone,
  archive_birth_date,
  orig_policy_retire_age_code,
  is_use_new_policy_retire_age,
  new_policy_retire_age,
  start_work_date,
  industry_entry_date,
  entry_date,
  company_entry_date,
  industry_entry_type_code,
  company_entry_type_code,
  archival_custodian_code,
  unit_center_record,
  labor_type_code,
  employment_form_code,
  eoh_person_category_code,
  whether_register,
  contract_sign_unit,
  full_time_diploma_code,
  full_time_degree_code,
  on_job_diploma_code,
  on_job_degree_code,
  highest_technology_name,
  highest_technology_level_code,
  highest_skill_name,
  highest_skill_level_code,
  employee_code,
  person_code,
  person_status_code,
  post_type_code,
  position_hierarchy_code
FROM
  (
    SELECT
      emp.*,
      org.SYSTEM_CODE AS orgCode,
      edu.highest_diploma_code,
      edu.highest_degree_code,
      edu.full_time_diploma_code,
      edu.full_time_degree_code,
      edu.on_job_diploma_code,
      edu.on_job_degree_code,
      tech.highest_technology_name,
      tech.highest_technology_level_code,
      skill.highest_skill_name,
      skill.highest_skill_level_code,
      post.post_type_code AS post_type_code,
      curpos.position_hierarchy_code AS position_hierarchy_code
    FROM
      T_DYNAMIC_BIZ_PBM_EMP_BASIC_INFO emp
      LEFT JOIN BIZ_PBM_ORGANIZATION org ON org.id = emp.department_id
      LEFT JOIN (
        SELECT
          e.employee_id,
          e.highest_diploma_code,
          e.highest_degree_code,
          e.full_time_diploma_code,
          e.full_time_degree_code,
          e.on_job_diploma_code,
          e.on_job_degree_code,
          ROW_NUMBER() OVER (
            PARTITION BY
              e.employee_id
            ORDER BY
              e.id DESC
          ) AS rn
        FROM
          T_DYNAMIC_BIZ_PBM_EMP_EDUCATION_HIGHEST e
      ) edu ON edu.employee_id = emp.id
      AND edu.rn = 1
      LEFT JOIN (
        SELECT
          t.employee_id,
          t.highest_technology_name,
          t.highest_technology_level_code,
          ROW_NUMBER() OVER (
            PARTITION BY
              t.employee_id
            ORDER BY
              t.id DESC
          ) AS rn
        FROM
          T_DYNAMIC_BIZ_PBM_EMP_TECHNOLOGY_HIGHEST t
      ) tech ON tech.employee_id = emp.id
      AND tech.rn = 1
      LEFT JOIN (
        SELECT
          s.employee_id,
          s.highest_skill_name,
          s.highest_skill_level_code,
          ROW_NUMBER() OVER (
            PARTITION BY
              s.employee_id
            ORDER BY
              s.id DESC
          ) AS rn
        FROM
          T_DYNAMIC_BIZ_PBM_EMP_SKILL_HIGHEST s
      ) skill ON skill.employee_id = emp.id
      AND skill.rn = 1
      LEFT JOIN BIZ_PBM_POST post ON post.id = CAST(emp.post_id AS BIGINT)
      LEFT JOIN (
        SELECT
          c.employee_id,
          c.position_hierarchy_code,
          c.current_hierarchy_date
        FROM
          (
            SELECT
              p.employee_id,
              p.position_hierarchy_code,
              p.current_hierarchy_date,
              ROW_NUMBER() OVER (
                PARTITION BY
                  p.employee_id
                ORDER BY
                  p.current_position_date DESC NULLS LAST,
                  p.id DESC
              ) AS rn
            FROM
              T_DYNAMIC_BIZ_PBM_EMP_POSITION p
            WHERE
              p.whether_current = 1
          ) c
        WHERE
          c.rn = 1
      ) curpos ON curpos.employee_id = emp.id
    WHERE
      emp.sys_deleted = 0
    ORDER BY
      CASE
        WHEN org.SYSTEM_CODE IS NULL THEN 1
        ELSE 0
      END,
      org.tree_sort,
      CASE
        WHEN curpos.position_hierarchy_code IS NULL THEN 1
        ELSE 0
      END,
      CASE
        WHEN curpos.position_hierarchy_code LIKE 'dict_u_w_p_layer%' THEN CAST(
          REPLACE (
            curpos.position_hierarchy_code,
            'dict_u_w_p_layer_',
            ''
          ) AS INT
        )
        ELSE 999
      END,
      CASE
        WHEN curpos.current_hierarchy_date IS NULL THEN 1
        ELSE 0
      END,
      curpos.current_hierarchy_date,
      emp.id
  ) AS __base__
WHERE
  1 = 1
LIMIT
  ?
OFFSET
  ?
